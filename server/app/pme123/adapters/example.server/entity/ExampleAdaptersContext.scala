package pme123.adapters.example.server.entity

import com.typesafe.config.{Config, ConfigFactory}
import pme123.adapters.server.entity.AdaptersContext.{settings => aSettings}
import pme123.adapters.server.entity.AdaptersContextPropsImplicits
import pme123.adapters.shared._

import scala.language.implicitConversions

/**
  * created by pascal.mengelt
  * This config uses the small framework typesafe-config.
  * See here the explanation: https://github.com/typesafehub/config
  */
object ExampleAdaptersSettings extends Logger {
  val configPath = "pme123.adapters.server"

  val httpContextProp = "play.http.context"
  val projectProp = "project"
  val runModeProp = "run.mode"
  val timezoneProp = "timezone"

  val mailHostProp = "mail.host"
  val mailPortProp = "mail.port"
  val mailSmtpTlsProp = "mail.smtp.tls"
  val mailSmtpSslProp = "mail.smtp.ssl"
  val mailUsernameProp = "mail.username"
  val mailPasswordProp = "mail.password"
  val mailFromProp = "mail.from"
  val adminMailActiveProp = "admin.mail.active"
  val adminMailRecipientProp = "admin.mail.recipient"
  val adminMailSubjectProp = "admin.mail.subject"
  val adminMailLoglevelProp = "admin.mail.loglevel"
  val processLogEnabledProp = "process.log.enabled"
  val processLogPathProp = "process.log.path"
  val wsocketHostsAllowedProp = "wsocket.hosts.allowed"

  val jobConfigsProp = "job.configs"

  def config(): Config = {
    ConfigFactory.invalidateCaches()
    ConfigFactory.load()
  }
}

// this settings will be validated on startup
class ExampleAdaptersSettings(config: Config) extends Logger {

  import ExampleAdaptersSettings._

  // checkValid(), just as in the plain SimpleLibContext
  config.checkValid(ConfigFactory.defaultReference(), configPath)

  val projectConfig: Config = config.getConfig(configPath)
  // Here go the custom config

}

// This is a different way to do AdaptersContext, using the
// AdaptersSettings class to encapsulate and validate the
// settings on startup
class ExampleAdaptersContext(config: Config)
  extends AdaptersContextPropsImplicits {

  val name: String = aSettings.project

  lazy val settings = new ExampleAdaptersSettings(config)

  lazy val props: Seq[AdaptersContextProp] =
    Seq(
      // no config yet
    )
}

// default Configuration
object ExampleAdaptersContext
  extends ExampleAdaptersContext(ExampleAdaptersSettings.config()) {
  ExampleAdaptersContext.logSettings
}
