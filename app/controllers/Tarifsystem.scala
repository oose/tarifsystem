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

  def list() = Action {
    Ok(Json.toJson(gebuehren))
  }
}