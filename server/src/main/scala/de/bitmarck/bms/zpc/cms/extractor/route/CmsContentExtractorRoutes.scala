package de.bitmarck.bms.zpc.cms.extractor.route

import cats.effect.IO
import de.bitmarck.bms.zpc.cms.extractor.Fs2Cms
import de.lolhens.http4s.errors.ErrorResponseEncoder.stacktrace._
import de.lolhens.http4s.errors.ErrorResponseLogger
import de.lolhens.http4s.errors.syntax._
import fs2.{Chunk, Stream}
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response, Status}
import org.log4s.getLogger

object CmsContentExtractorRoutes {
  private val logger = getLogger
  private implicit val throwableLogger: ErrorResponseLogger[Throwable] = ErrorResponseLogger.throwableLogger(logger.logger)

  def toRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "health" => Ok()

    case request@POST -> Root =>
      (for {
        chunk <- request.body
          .through(Fs2Cms.cmsSignedDataContent())
          .compile.foldChunks(Chunk.empty[Byte])(_ ++ _)
          .orErrorResponse(Status.BadRequest)
      } yield
        Response[IO]()
          .withEntity(Stream.chunk(chunk).covary[IO]))
        .merge

    case request@POST -> Root / "base64" =>
      (for {
        chunk <- request.body
          .through(fs2.text.utf8.decode)
          .through(fs2.text.base64.decode)
          .through(Fs2Cms.cmsSignedDataContent())
          .compile.foldChunks(Chunk.empty[Byte])(_ ++ _)
          .orErrorResponse(Status.BadRequest)
      } yield
        Response[IO]()
          .withEntity(Stream.chunk(chunk).covary[IO]))
        .merge
  }
}
