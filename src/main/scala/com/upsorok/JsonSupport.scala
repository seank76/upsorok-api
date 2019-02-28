package com.upsorok

import java.time.Instant
import java.util.UUID

import com.upsorok.address.{Address, Country, USState}
import com.upsorok.business.{Business, Businesses}
import com.upsorok.business.BusinessActor.SaveBusiness
import com.upsorok.review.Review
import com.upsorok.review.ReviewActor.SaveReview
import com.upsorok.user.UserActor.ActionPerformed
import com.upsorok.user.{Name, User, UserType, Users}
import spray.json.{DeserializationException, JsArray, JsString, JsValue, JsonFormat}

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw new DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    def write(instant: Instant) = JsString(instant.toString)
    def read(value: JsValue): Instant = {
      value match {
        case JsString(instantString) => Instant.parse(instantString)
        case _ => throw new DeserializationException("Expected Instant string")
      }
    }
  }

  implicit object USStateFormat extends JsonFormat[USState] {
    def write(state: USState) = JsString(state.short)
    def read(value: JsValue): USState = {
      value match {
        case JsString(stateString) => USState.withName(stateString)
        case _ => throw new DeserializationException("Expected State string")
      }
    }
  }

  implicit object CountryFormat extends JsonFormat[Country] {
    def write(country: Country) = JsString(country.short)
    def read(value: JsValue): Country = {
      value match {
        case JsString(countryString) => Country.withName(countryString)
        case _ => throw new DeserializationException("Expected Country string")
      }
    }
  }

  implicit object UserTypeSetFormat extends JsonFormat[Set[UserType]] {
    def write(userTypes: Set[UserType]) = JsArray(userTypes.map(t => JsString(t.entryName)).toVector)
    def read(value: JsValue): Set[UserType] = {
      value match {
        case JsArray(array) => array.map {
          case JsString(entry) => UserType.withNameLowercaseOnly(entry.toLowerCase)
          case _ => throw new DeserializationException("Expected JsString")
        }.toSet[UserType]
        case _ => throw new DeserializationException("Expected UserType set")
      }
    }
  }

  implicit val nameJsonFormat = jsonFormat3(Name)
  implicit val addressJsonFormat = jsonFormat5(Address)
  implicit val businessJsonFormat = jsonFormat3(Business)
  implicit val businessesJsonFormat = jsonFormat1(Businesses)
  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val saveReviewJsonFormat = jsonFormat4(SaveReview)
  implicit val reviewJsonFormat = jsonFormat7(Review)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val saveBusinessJsonFormat = jsonFormat2(SaveBusiness)
}
//#json-support
