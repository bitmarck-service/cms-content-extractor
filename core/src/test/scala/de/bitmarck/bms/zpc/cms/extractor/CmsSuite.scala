package de.bitmarck.bms.zpc.cms.extractor

import cats.effect.IO
import fs2._

class CmsSuite extends CatsEffectSuite {
  private def resourceStream(name: String): Stream[IO, Byte] =
    fs2.io.readInputStream(IO(getClass.getClassLoader.getResourceAsStream(name)), Fs2Cms.defaultChunkSize)

  test("valid content") {
    resourceStream("content-valid")
      .through(Fs2Cms.cmsSignedDataContent())
      .compile
      .foldChunks(Chunk.empty[Byte])(_ ++ _)
  }

  test("content too short".fail) {
    resourceStream("content-too-short")
      .through(Fs2Cms.cmsSignedDataContent())
      .compile
      .foldChunks(Chunk.empty[Byte])(_ ++ _)
  }

  test("content too long".fail) {
    resourceStream("content-too-long")
      .through(Fs2Cms.cmsSignedDataContent())
      .compile
      .foldChunks(Chunk.empty[Byte])(_ ++ _)
  }
}
