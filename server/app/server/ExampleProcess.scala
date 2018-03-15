package server

import java.time.LocalDateTime
import javax.inject.Inject

import akka.actor.ActorRef
import akka.stream.Materializer
import pme123.adapters.server.control.demo.DemoService.toISODateTimeString
import pme123.adapters.server.control.{JobProcess, LogService}
import pme123.adapters.shared.LogLevel.{DEBUG, ERROR, INFO, WARN}
import pme123.adapters.shared._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait ExampleProcess extends JobProcess {

  def jobLabel: String

  def createInfo(): ProjectInfo = // same version as the adapters!
    createInfo(pme123.adapters.version.BuildInfo.version)

  // the process fakes some long taking tasks that logs its progress
  def runJob(user: String)
            (implicit logService: LogService
             , jobActor: ActorRef): Future[LogService] = {
    Future {
      logService.startLogging()
      val results =
        for {
          i <- 2 to 3
          k <- 1 to 5
        } yield ExampleResult(s"Image Gallery $i - $k"
          , s"https://www.gstatic.com/webp/gallery$i/$k.png"
          , toISODateTimeString(LocalDateTime.now().minusHours(Random.nextInt(100))))

      results.foreach(doSomeWork)

      logService
    }
  }

  protected def doSomeWork(dr: ExampleResult)
                          (implicit logService: LogService): LogEntry = {
    Thread.sleep(500)
    val ll = Random.shuffle(List(DEBUG, DEBUG, INFO, INFO, INFO, WARN, WARN, ERROR)).head
    val detail = List(None, Some(s"Details for $jobLabel $ll: ${dr.name}"), Some("shell_session_save_history () \n{ \n    shell_session_history_enable;\n    history -a;\n    if [ -f \"$SHELL_SESSION_HISTFILE_SHARED\" ] && [ ! -s \"$SHELL_SESSION_HISTFILE\" ]; then\n        echo -ne '\\n...copying shared history...';\n        ( umask 077;\n        /bin/cp \"$SHELL_SESSION_HISTFILE_SHARED\" \"$SHELL_SESSION_HISTFILE\" );\n    fi;\n    echo -ne '\\n...saving history...';\n    ( umask 077;\n    /bin/cat \"$SHELL_SESSION_HISTFILE_NEW\" >> \"$SHELL_SESSION_HISTFILE_SHARED\" );\n    ( umask 077;\n    /bin/cat \"$SHELL_SESSION_HISTFILE_NEW\" >> \"$SHELL_SESSION_HISTFILE\" );\n    : >|\"$SHELL_SESSION_HISTFILE_NEW\";\n    if [ -n \"$HISTFILESIZE\" ]; then\n        echo -n 'truncating history files...';\n        HISTFILE=\"$SHELL_SESSION_HISTFILE_SHARED\";\n        HISTFILESIZE=\"$HISTFILESIZE\";\n        HISTFILE=\"$SHELL_SESSION_HISTFILE\";\n        HISTFILESIZE=\"$size\";\n        HISTFILE=\"$SHELL_SESSION_HISTFILE_NEW\";\n    fi;\n    echo -ne '\\n...'\n}"))(Random.nextInt(3))
    logService.log(ll, s"Job: $jobLabel $ll: ${dr.name}", detail)
  }
}

class ExampleJobProcess @Inject()()(implicit val mat: Materializer, val ec: ExecutionContext)
  extends ExampleProcess {
  override def jobLabel: String = "Example Job"
}

class ExampleJobWithDefaultSchedulerActor @Inject()()(implicit val mat: Materializer, val ec: ExecutionContext)
  extends ExampleProcess {
  val jobLabel = "Example Job with Default Scheduler"
}

class ExampleJobWithoutSchedulerActor @Inject()()(implicit val mat: Materializer, val ec: ExecutionContext)
  extends ExampleProcess {
  val jobLabel = "Example Job without Scheduler"

}
