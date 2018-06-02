package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the sign up process.
 */

object SignUpForm {

  /**
   * A play framework form.
   */
  val form = Form[Data](
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText,
      // "password" -> nonEmptyText
      //      "password" -> mapping(
      "main" -> nonEmptyText,
      "confirm" -> nonEmptyText
    //.verifying("Passwords don't match",password  => password.main == password.confirm)
    //    )(PassData.apply)(PassData.unapply)
    //verifying("Passwords don't match", password  => password.main == password.confirm)
    //(PassData.apply)(PassData.unapply)
    )(Data.apply)(Data.unapply)
      verifying ("Passwords don't match", password => password.main == password.confirm)
  )

  // The mapping signature doesn't match the User case class signature,
  // so we have to define custom binding/unbinding functions
  //    {
  // Binding: Create a User from the mapping result (ignore the second password and the accept field)
  //      (firstName, lastName, email, passwords, _) => Data2(firstName, lastName, email, passwords._1) 
  //    } 

  // dbへのinsert
  //  def insert(data: Data): Unit = SignUpFormSupport.insert(data)

  /**
   * The form data.
   *
   * @param firstName The first name of a user.
   * @param lastName The last name of a user.
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class Data(
    firstName: String,
    lastName: String,
    email: String,
    //password: forms.SignUpForm.PassData
    //  )

    //  case class PassData(
    main: String,
    confirm: String
  )

}

