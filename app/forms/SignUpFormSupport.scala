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

//以下最初の実装からの移植

import com.typesafe.config.ConfigFactory

//import util.control.Exception._
//import java.sql.Connection
import scalikejdbc._
//import scalikejdbc.config._
import scalikejdbc.SQLInterpolation._
//import org.mariadb.jdbc.Driver

//import forms.SignUpForm._

object SignUpFormSupport {

  import forms.SignUpForm._

  /** 登録 */
  def insert(data: Data): Unit = {

    try {

      DB localTx { implicit session =>

        import data._
        //import org.mariadb.jdbc.Driver
        //import scalikejdbc.config._

        val id = System.currentTimeMillis()

        //        val firstName = Some(data.firstName).toString
        //        val lastName = Some(data.lastName).toString
        //        val email = Some(data.email).toString
        //        val password2 = password.main

        sql"""insert into customer1 (id, `name`, email, `password`)
                  |values ($id,  ${firstName + lastName}, $email, $main)"""
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

}
