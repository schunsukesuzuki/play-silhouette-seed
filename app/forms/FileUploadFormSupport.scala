package forms

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import models.User
import models.services.{ AuthTokenService, UserService }
//import org.webjars.play.WebJarsUtil
import play.api.i18n.{ I18nSupport, Messages }
import play.api.libs.mailer.{ Email, MailerClient }

import play.api.mvc._
//import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents, Request }
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

//execution contextのための追加を最下部にした
//import scala.concurrent.ExecutionContext.Implicits.global

//以下最初の実装からの移植
import com.typesafe.config.ConfigFactory
//import util.control.Exception._
import java.sql.Connection
import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.SQLInterpolation._
//import org.mariadb.jdbc.Driver

//import forms.SignUpForm._

//fileuploadのための追加

import java.io.File
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{ Files, Path }
import java.util
import javax.inject._

//フォルダ作成と書き込み保存のための追加
import java.nio.file.{ Paths, Files }
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.io.{ File => JFile } // リネームして区別しやすくする

//import akka.stream.IOResult
//正体不明
//import akka.stream.scaladsl._
//import akka.util.ByteString

//akkaのファイルを軒並み読み込んだらエラーが2つ減った
import akka._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util._

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.streams._
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.FileInfo

// better filesのための追加
import better.files._
import File._

//画像の保存が必要。

//import scala.io.Source
//import java.io.PrintWriter

object FileUploadFormSupport {

  //https://anopara.net/2017/07/19/scala%E3%81%A7akka-stream%E3%82%92%E4%BD%BF%E3%81%A3%E3%81%A6%E3%81%A7%E3%81%8B%E3%81%84%E3%83%87%E3%83%BC%E3%82%BF%E3%82%92%E3%81%84%E3%81%84%E6%84%9F%E3%81%98%E3%81%AB%E5%87%A6%E7%90%86%E3%81%99/
  private[this] implicit val actorSystem = ActorSystem()
  private[this] implicit val materializer = ActorMaterializer()
  private[this] implicit val context = actorSystem.dispatcher

  import forms.FileUploadForm._

  /** 登録 */
  def picsave(picFormData: PicFormData): Unit = {

    try {

      DB localTx { implicit session =>

        import picFormData._
        //import org.mariadb.jdbc.Driver
        //import scalikejdbc.config._

        // request.identityをどこかで挿入して、firstName + lastName = `name` の関係からnameフォルダ名にして自動生成。できればログイン名をcustomer2で作ってこれをフォルダ名に。cutomer2の登録を実装してから変更するのでも良さそう。

        //https://www.qoosky.io/techs/f7851bb2e4

        // 新規ファイル作成
        //    val file = Paths.get("sample.txt")

        val picName2: java.nio.file.Path = picName.asInstanceOf[java.nio.file.Path]

        if (Files.notExists(picName2)) Files.createFile(picName2)
        //    if(Files.notExists(file)) Files.createFile(file)

        //http://seratch.hatenablog.jp/entry/2013/02/28/220754
        //sqlinterpolationのselect文での使い方がいまいちわかっていない。 val から始まる文である場合、rs(resultset)をmapする必要があるんだろうか。

        //[Name]はたぶんいらない（サンプルではcase classでNameにあたる型を定義してる)
        val name = sql"select `name` from customer1 where email"
        //    val name: Option[Name] = sql"select `name` from customer1 where email"
        //        .map(rs => Name(rs)).single.apply()
        //          .stripMargin
        //          .update
        //          .apply()

        // 新規ディレクトリ作成。アルバムを作るときの条件分岐も欲しい。
        //val dir = Paths.get("mydir")
        val dirp = Paths.get(s"{$name}")
        //    if(Files.notExists(dir)) Files.createDirectory(dir) // mkdir
        if (Files.notExists(dirp)) Files.createDirectories(dirp) // mkdir -p

        //akkaを使った部分
        //        val parallelism = 16

        //  val source = Source{
        //srcDirをpinNameに変更。orが正しければmgegを追加したい。
        //list recursively はhttps://github.com/pathikrit/better-files/blob/master/core/src/main/scala/better/files/File.scala
        // toLowerCaseは大文字を小文字へ変換
        // toStreamでStreamに変換(scalaのコレクションメソッド)
        //    dirp.listRecursively.filter(f => f.extension.map(_.toLowerCase()).contains(".jpg"|".png") ).toStream
        //    srcDir.listRecursively.filter(f => f.extension.map(_.toLowerCase()).contains(".jpg") ).toStream
        //  }

        //import better.files._
        //import File._

        //        val sink: Sink[File, Future[Done]] =
        //destDirをdirpに変更
        //http://tototoshi.hatenablog.com/entry/2015/12/23/154104
        //          Sink.foreachParallel(parallelism)(resizeAndCopy(_, dirp))
        //    Sink.foreachParallel(parallelism)(resizeAndCopy(_, destDir))
        //        val graph = source.runWith(sink)

        //        graph.onComplete { _ =>
        //          Await.result(actorSystem.terminate(), 10 seconds)
        //  }
        //akkaを使った部分終了

        //          Files.move(file, file, REPLACE_EXISTING) // 存在していれば上書き
        //    Files.move(file, dirp.resolve(file.getFileName), REPLACE_EXISTING) // ディレクトリ間の移動

        // 存在すれば削除
        //          Files.deleteIfExists(dirp.resolve(file.getFileName))

        // graph.onCompleteの中にFilesメソッドのカッコの中に入れた

        //        }
        // 書き込み(恐らくテキストファイルの書き込みだけなのでダメ)
        //http://takezoe.hatenablog.com/entry/2016/01/27/155542
        //

        //    val pw = new PrintWriter(s"$name/$picName")
        //    pw.write("Hello, world")
        //    pw.close

        //sqlで画像の名前を保存。いずれtoxi法でタグテーブルを別途作成。
        val id = System.currentTimeMillis()

        //        val firstName = Some(data.firstName).toString
        //        val lastName = Some(data.lastName).toString
        //        val email = Some(data.email).toString
        //        val password2 = password.main

        sql"""insert into fileupload1 (id, pic_name)
                  |values ($id,  ${picName})"""
          .stripMargin
          .update
          .apply()

        //        sql"""insert into customer (id, `status`, `name`, sex_type, zip_code, pref_code, city_name, address_name, building_name, email, phone, login_name, `password`, favorite_category_id, version)
        //                  |values ($id, $status, $name, $sexType,${zipCode1 + zipCode2}, $prefCode, $cityName, $addressName, $buildingName, $email, $phone, $loginName, $password, $favoriteCategoryId, $version)"""

      }

    } catch {
      case e: Exception => e.printStackTrace()

    }

  }

  //  def mkdirandsave(picFormData: PicFormData): Unit = {

  //  }

}

