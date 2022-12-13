package de.bitmarck.bms.zpc.cms.extractor

import cats.effect._
import com.comcast.ip4s._
import de.bitmarck.bms.zpc.cms.extractor.route.CmsContentExtractorRoutes
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.ErrorAction
import org.log4s.getLogger

import scala.concurrent.duration._

object Main extends IOApp {
  private val logger = getLogger

  override def run(args: List[String]): IO[ExitCode] =
    applicationResource.use(_ => IO.never)

  def applicationResource: Resource[IO, Unit] =
    for {
      _ <- serverResource(
        SocketAddress(host"0.0.0.0", port"8080"),
        CmsContentExtractorRoutes.toRoutes.orNotFound
      )
    } yield ()

  def serverResource[F[_] : Async](socketAddress: SocketAddress[Host], http: HttpApp[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(socketAddress.host)
      .withPort(socketAddress.port)
      .withHttpApp(
        ErrorAction.log(
          http = http,
          messageFailureLogAction = (t, msg) => Async[F].delay(logger.debug(t)(msg)),
          serviceErrorLogAction = (t, msg) => Async[F].delay(logger.error(t)(msg))
        ))
      .withShutdownTimeout(1.second)
      .build
}
