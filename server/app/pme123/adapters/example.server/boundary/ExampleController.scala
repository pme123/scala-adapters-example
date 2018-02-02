package pme123.adapters.example.server.boundary

import javax.inject._

import controllers.AssetsFinder
import play.api.Configuration
import play.api.mvc._
import pme123.adapters.server.control.http.SameOriginCheck
import pme123.adapters.server.entity.AdaptersContext.settings.httpContext

import scala.concurrent.ExecutionContext

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class ExampleController @Inject()(template: views.html.index
                                  , assetsFinder: AssetsFinder
                                  , cc: ControllerComponents
                                  , val config: Configuration)
                                 (implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with SameOriginCheck {

  // Home page that renders template
  def index = Action { implicit request: Request[AnyContent] =>
    val context = if (httpContext.length > 1)
      httpContext
    else
      ""
    // uses the AssetsFinder API
    Ok(template(context, assetsFinder))
  }

}

