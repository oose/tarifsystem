package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits._
import org.joda.time.Interval
import org.joda.time.DateTime
import play.api.data.Form


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

  val calls = List(routes.Tarifsystem.gebuehr("strafzettel").url,
    routes.Tarifsystem.listGebuehr().url,
    routes.Tarifsystem.km("Kompakt").url,
    routes.Tarifsystem.listKm.url,
    routes.Tarifsystem.zeit("proWoche").url,
    routes.Tarifsystem.listZeit.url,
    routes.Tarifsystem.berechneTarif("2014-01-01", "2014-01-02", "2014-01-01", "2014-01-04", 100, "Kleinwagen", "m").url)

  def index = Action {
    val patterns = for {
      routes <- play.api.Play.current.routes.toList
      (method, pattern, call) <- routes.documentation if (!pattern.startsWith("/webjars"))
    } yield {
      (method, pattern, call)
    }
    val callList = (calls map (url => s"""<li> <a href="${url}">${url}</a>""")).mkString
    Ok(s"""
<html>
    <head>
        <link rel='stylesheet' href='${routes.WebJarAssets.at(WebJarAssets.locate("css/bootstrap.min.css"))}' />
    </head>
    <body>
    	<div class="page-header">
    		<h1>Tarifsystem <small>documentation</small></h1>
    	</div>
    	<h1>Examples: calls</h1>
    	<ul>
    		${callList}
    	</ul>
     	<h1>API:</h1>
        <ul>
    		${patterns.map{case (m, p, c) => s"""<li><span class="text-info">${m}</span>, <span>${p}</span></li>"""}.mkString}
    </body>
</html>
        """).as("text/html")
  }

  def gebuehr(art: String) = Action {
    Async {
      scala.concurrent.future {
        gebuehren.get(art) match {
          case Some(betrag) =>
            Ok(Json.parse(s"""
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

  def berechneTarif(rvon: String, rbis: String, fvon: String, fbis: String, km: Int, kfzKlasse: String, geschlecht: String) = Action {
    Async {
      scala.concurrent.future {
        val resDauer: Duration = (rvon, rbis)
        val fahrtDauer: Duration = (fvon, fbis)
        val gesamtDauer = Duration.maxDauer(resDauer, fahrtDauer).toPeriod().getDays() + 1

        (for (
          zeittarif <- zeittarife.get("proTag");
          kmPreis <- kmTarife.get(kfzKlasse);
          preis <- zeittarif.get(kfzKlasse)
        ) yield { (kmPreis, preis) } match {
          case (kmPreis, zeitPreis) => {
            val total = kmPreis * km + zeitPreis * gesamtDauer
            Ok(Json.parse(s"""
    		      { "preis": ${total} ,
                    "gesamtDauer": ${gesamtDauer},
                    "kmPreis": ${kmPreis},
                    "zeitPreis": ${zeitPreis},
                    "gefahreneKm": ${km} }
    		      """))
          }
        }).getOrElse(BadRequest)

      }
    }
  }
}

case class Duration(from: DateTime, until: DateTime) {
  assert(isValid)
  def isValid = (from isBefore until) || (from isEqual until)
}

object Duration {
  implicit def getInterval(duration: Duration): Interval = new Interval(duration.from, duration.until)
  implicit def toDurationFromString(d: (String, String)): Duration = Duration(DateTime.parse(d._1), DateTime.parse(d._2))
  def maxDauer(z1: Duration, z2: Duration) = Duration(if (z1.from.isBefore(z2.from)) z1.from else z2.from, if (z1.until.isAfter(z2.until)) z1.until else z2.until)

}