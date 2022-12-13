package de.bitmarck.bms.zpc.cms.extractor

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    fs2.io.readInputStream(IO.pure(System.in), Fs2Cms.defaultChunkSize, closeAfterUse = false)
      .through(Fs2Cms.cmsSignedDataContent())
      .through(fs2.io.writeOutputStream[IO](IO.pure(System.out), closeAfterUse = false))
      .compile
      .drain
      .start.flatMap(_.joinWithNever) // because readInputStream is uncancelable
      .as(ExitCode.Success)
  }
}
