package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._

object Tarifsystem extends Controller {

  val gebuehren = Map(("monatsbeitrag" -> 9.90),
    ("entschaedigung" -> -25.00),
    ("verspätung_gemeldet" -> 30.0),
    ("verspätung_ungemeldet" -> 50.0),
    ("strafzettel" -> 5.00))

  val kmTarife = Map("Kleinwagen" -> 0.13,
    "Kompakt" -> 0.15,
    "Komfort" -> 0.17,
    "Extra" -> 0.19)

  val zeittarife = Map(
    "proTag" -> Map(
      "Kleinwagen" -> 25.90,
      "Kompakt" -> 34.90,
      "Komfort" -> 39.90,
      "Extra" -> 47.90),
    "frauenNachtAuto" -> Map(
      "Kleinwagen" -> 0.0,
      "Kompakt" -> 0.0,
      "Komfort" -> 0.0,
      "Extra" -> 2.5),
    "proWoche" -> Map(
      "Kleinwagen" -> 149.0,
      "Kompakt" -> 199.0,
      "Komfort" -> 229.0,
      "Extra" -> 279.0),
    "proMonat" -> Map(
      "Kleinwagen" -> 498.0,
      "Kompakt" -> 655.00,
      "Komfort" -> 765.0,
      "Extra" -> 919.0))

  def gebuehr(art: String) = Action {
    Async {
      scala.concurrent.future {
        gebuehren.get(art) match {
          case Some(betrag) => Ok(Json.parse(s"""
	        {
	    		"${art}": ${betrag}
	    		}
	        """))
          case None => BadRequest(Json.toJson(Map("error" -> "Gebuehrenart unbekannt")))
        }
      }
    }

  }

  def listGebuehr() = Action {
    Ok(Json.toJson(gebuehren))
  }

  def km(art: String) = Action {
    Async {
      scala.concurrent.future {
        kmTarife.get(art) match {
          case Some(betrag) => Ok(Json.parse(s"""
	        {
	    		"${art}": ${betrag}
	    		}
	        """))
          case None => BadRequest(Json.toJson(Map("error" -> "Km-Art unbekannt")))
        }
      }
    }

  }

  def listKm() = Action {
    Ok(Json.toJson(kmTarife))
  }

  def zeit(art: String) = Action {
    Async {
      scala.concurrent.future {
        zeittarife.get(art) match {
          case Some(tarife) => Ok(Json.toJson(tarife))
          case None => BadRequest(Json.toJson(Map("error" -> "Zeit-Art unbekannt")))
        }
      }
    }

  }

  def listZeit() = Action {
    Ok(Json.toJson(zeittarife))
  }
}