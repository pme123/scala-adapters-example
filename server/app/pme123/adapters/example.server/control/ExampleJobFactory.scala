package pme123.adapters.example.server.control

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import pme123.adapters.server.control.{JobActor, JobActorFactory}
import pme123.adapters.server.entity.ServiceException
import pme123.adapters.shared.JobConfig.JobIdent

import scala.concurrent.ExecutionContext

@Singleton
class ExampleJobFactory @Inject()(exampleJob: ExampleJobProcess
                                  , exampleJobWithDefaultScheduler: ExampleJobWithDefaultSchedulerActor
                                  , exampleJobWithoutScheduler: ExampleJobWithoutSchedulerActor
                                  , system: ActorSystem
                              )(implicit val mat: Materializer
                                , val ec: ExecutionContext)
  extends JobActorFactory {

  import ExampleJobFactory._

  private lazy val exampleJobRef = system.actorOf(JobActor.props(exampleJobIdent, exampleJob))
  private lazy val exampleJobWithDefaultSchedulerRef = system.actorOf(JobActor.props(exampleJobWithDefaultSchedulerIdent, exampleJobWithDefaultScheduler))
  private lazy val exampleJobWithoutSchedulerRef = system.actorOf(JobActor.props(exampleJobWithoutSchedulerIdent, exampleJobWithoutScheduler))

  def jobActorFor(jobIdent: JobIdent): ActorRef = jobIdent match {
    case "exampleJob" => exampleJobRef
    case "exampleJobWithDefaultScheduler" => exampleJobWithDefaultSchedulerRef
    case "exampleJobWithoutScheduler" => exampleJobWithoutSchedulerRef
    case other => throw ServiceException(s"There is no Job for $other")
  }

}

object ExampleJobFactory {
  val exampleJobIdent: JobIdent = "exampleJob"
  val exampleJobWithDefaultSchedulerIdent: JobIdent = "exampleJobWithDefaultScheduler"
  val exampleJobWithoutSchedulerIdent: JobIdent = "exampleJobWithoutScheduler"
}
