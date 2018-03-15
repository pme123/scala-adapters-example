package server

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import pme123.adapters.server.control.{JobActor, JobCreation}
import pme123.adapters.server.entity.AdaptersContext.settings.jobConfigs
import pme123.adapters.server.entity.ServiceException
import pme123.adapters.shared.JobConfig
import pme123.adapters.shared.JobConfig.JobIdent

import scala.concurrent.ExecutionContext

@Singleton
class ExampleJobCreation @Inject()(exampleJob: ExampleJobProcess
                                   , exampleJobWithDefaultScheduler: ExampleJobWithDefaultSchedulerActor
                                   , exampleJobWithoutScheduler: ExampleJobWithoutSchedulerActor
                                   , @Named("actorSchedulers") val actorSchedulers: ActorRef
                                   , actorSystem: ActorSystem
                                  )(implicit val mat: Materializer
                                    , val ec: ExecutionContext)
  extends JobCreation {

  private val exampleJobIdent: JobIdent = "exampleJob"
  private val exampleJobWithDefaultSchedulerIdent: JobIdent = "exampleJobWithDefaultScheduler"
  private val exampleJobWithoutSchedulerIdent: JobIdent = "exampleJobWithoutScheduler"


  private lazy val exampleJobRef = actorSystem.actorOf(JobActor.props(jobConfigs(exampleJobIdent), exampleJob), exampleJobIdent)
  private lazy val exampleJobWithDefaultSchedulerRef = actorSystem.actorOf(JobActor.props(jobConfigs(exampleJobWithDefaultSchedulerIdent), exampleJobWithDefaultScheduler), exampleJobWithDefaultSchedulerIdent)
  private lazy val exampleJobWithoutSchedulerRef = actorSystem.actorOf(JobActor.props(jobConfigs(exampleJobWithoutSchedulerIdent), exampleJobWithoutScheduler), exampleJobWithoutSchedulerIdent)

  def createJobActor(jobConfig: JobConfig): ActorRef = jobConfig.jobIdent match {
    case "exampleJob" => exampleJobRef
    case "exampleJobWithDefaultScheduler" => exampleJobWithDefaultSchedulerRef
    case "exampleJobWithoutScheduler" => exampleJobWithoutSchedulerRef
    case other => throw ServiceException(s"There is no Job for $other")
  }

}
