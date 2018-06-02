package controllers

import java.io.File
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{ Files, Path }
import java.util
import javax.inject._

//silhouetteのための追加

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._

// SecuredRequestがないと言われるので追加
//https://github.com/mohiva/play-silhouette-seed/blob/master/app/controllers/ChangePasswordController.scala
import com.mohiva.play.silhouette.api.actions.SecuredRequest

//uploadのActionを変更する際、WithProviderがないとエラーが出て追加
import utils.auth.{ DefaultEnv, WithProvider }

//SignUpControllerから追加
import forms.{ FileUploadForm, FileUploadFormSupport }
import models.User

//silhouetteのための追加
import models.services.{ AuthTokenService, UserService }

//akka stream
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.streams._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{ Email, MailerClient }

import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.{ ExecutionContext, Future }

//import scala.concurrent.ExecutionContext.Implicits.global

//case class FormData(name: String)

/**
 * This controller handles a file upload.
 */
//@Singleton
//class HomeController @Inject() (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends Controller with i18n.I18nSupport {

//SignUpControllerを参考にFileUploadControllerに変更
class FileUploadController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient,
  implicit val webJarAssets: WebJarAssets,
  //SocialProviderRegistryを追加
  socialProviderRegistry: SocialProviderRegistry,
  //futureのために ec: ExecutionContextを追加
  ec: ExecutionContext,
  //,
  //upload のActionの際に追加
  credentialsProvider: CredentialsProvider
)
  extends Controller with i18n.I18nSupport {

  //特別なimportは必要なさそう
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  //formsフォルダにあるので無視
  //  val form = Form(
  //    mapping(
  //      "name" -> text
  //    )(FormData.apply)(FormData.unapply)
  //  )

  /**
   * Renders a start page.
   */

  //  silhouette対応に変更
  //  def index = Action { implicit request =>
  //    Ok(views.html.index(form))
  //  }

  def fileuploadview = silhouette.SecuredAction.async { implicit request =>
    //  def fileuploadview = silhouette.UnsecuredAction.async { implicit request =>
    // signUpをfileUploadに、SignUpFormをFileUploadForm変更
    // さらにrequest.identityを持たせる
    Future.successful(Ok(views.html.fileUpload(request.identity, FileUploadForm.form)))
    //    Future.successful(Ok(views.html.fileUpload(FileUploadForm.form)))

  }

  type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]

  /**
   * Uses a custom FilePartHandler to return a type of "File" rather than
   * using Play's TemporaryFile class.  Deletion must happen explicitly on
   * completion, rather than TemporaryFile (which uses finalization to
   * delete temporary files).
   *
   * @return
   */
  //private 
  def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType) =>
      val attr = PosixFilePermissions.asFileAttribute(util.EnumSet.of(OWNER_READ, OWNER_WRITE))
      val path: Path = Files.createTempFile("multipartBody", "tempFile", attr)
      val file = path.toFile
      val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(path)
      val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
      accumulator.map {
        case IOResult(count, status) =>
          logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, file)
      }
  }

  /**
   * A generic operation on the temporary file that deletes the temp file after completion.
   */
  //private 
  def operateOnTempFile(file: File) = {
    val size = Files.size(file.toPath)
    logger.info(s"size = ${size}")
    Files.deleteIfExists(file.toPath)
    size
  }

  /**
   * Uploads a multipart file as a POST request.
   *
   * @return
   */

  //https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/libs/Files.scala

  def upload = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>

    //picNameはviewのformhelper、modelのメンバと一致しなければならない
    val fileOption = request.body.file("picName").map {

      case FilePart(key, filename, contentType, file) =>
        logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")

        //file.toPath
        val data = operateOnTempFile(file)

        //    file.ref.moveTo(new File(s"/tmp/picture/${filename}"))      

        data

    }

    //ここを導入するべきかわからない。適当に(_)を入れてしまったがこれが正しいかも不明。
    FileUploadFormSupport.picsave(_)
    //これをなくしてもファイルはupされない
    //http://d.hatena.ne.jp/plasticscafe/20100702/1278029177

    Ok(s"file size = ${fileOption.getOrElse("no file")}")
  }

}

