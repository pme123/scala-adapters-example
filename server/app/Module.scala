import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import pme123.adapters.example.server.control.ExampleJobFactory
import pme123.adapters.server.control.{JobActorFactory, JobActorScheduler, UserActor, UserParentActor}
import slogging.{LoggerConfig, SLF4JLoggerFactory}

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    // example
    bind(classOf[JobActorFactory]).to(classOf[ExampleJobFactory])
    // framework
    LoggerConfig.factory = SLF4JLoggerFactory()
    bindActor[UserParentActor]("userParentActor")
    bindActorFactory[UserActor, UserActor.Factory]
    bind(classOf[JobActorScheduler]).asEagerSingleton()

  }
}
