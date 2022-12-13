package de.bitmarck.bms.zpc.cms.extractor

import cats.effect.Async
import cats.syntax.applicativeError._
import cats.syntax.functor._
import fs2._
import fs2.io._
import org.bouncycastle.cms.CMSSignedDataParser
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder

import java.io.BufferedInputStream

object Fs2Cms {
  private[extractor] val defaultChunkSize: Int = 1024 * 64

  def cmsSignedDataContent[F[_] : Async](chunkSize: Int = defaultChunkSize): Pipe[F, Byte, Byte] = { stream =>
    stream
      .through(toInputStream[F]).map(new BufferedInputStream(_, chunkSize))
      .evalMap(in => Async[F].blocking {
        new CMSSignedDataParser(
          new JcaDigestCalculatorProviderBuilder().build(),
          in
        )
      }).flatMap { parser =>
      readInputStream[F](Async[F].blocking {
        parser.getSignedContent.getContentStream
      }, chunkSize) ++
        Stream.exec {
          Async[F].blocking {
            parser.getSignerInfos
          }.handleErrorWith { e =>
            Async[F].raiseError(new IllegalStateException("failed to parse signer infos", e))
          }.void
        }
    }
  }
}
