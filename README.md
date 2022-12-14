# CMSSignedData Content Extractor

[![Docker Workflow](https://github.com/bitmarck-service/cms-content-extractor/workflows/build/badge.svg)](https://github.com/bitmarck-service/cms-content-extractor/actions?query=workflow%3Abuild)
[![Release Notes](https://img.shields.io/github/release/bitmarck-service/cms-content-extractor.svg?maxAge=3600)](https://github.com/bitmarck-service/cms-content-extractor/releases/latest)
[![Apache License 2.0](https://img.shields.io/github/license/bitmarck-service/cms-content-extractor.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

A tool to extract the payload from CMSSignedData binary objects.
BouncyCastle is used to parse the binary data structures.

## CLI Usage

```sh
cat cms-signed-file.bin | cms-content-extractor > cms-payload.bin
```

## WebService Usage

| Endpoint       | Input                                | Output               | Description                                    |
|----------------|--------------------------------------|----------------------|------------------------------------------------|
| `GET /health`  | -                                    | 200 Ok               | Healthcheck Endpoint                           |
| `POST /`       | CMSSignedData: `ByteArray`           | Payload: `ByteArray` | Extracts the payload from CMSSignedData        |
| `POST /base64` | CMSSignedData Base64: `Base64String` | Payload: `ByteArray` | Extracts the payload from CMSSignedData Base64 |

## Released Artifacts

The latest version is available [here](https://github.com/bitmarck-service/cms-content-extractor/releases/latest)

| Name                                   | Description                          |
|----------------------------------------|--------------------------------------|
| cms-content-extractor-cli-x.y.z        | Linux x64 binary for (fast startup)  |
| cms-content-extractor-cli-x.y.z.sh.bat | Platform independent Java executable |

## License

This project uses the Apache 2.0 License. See the file called LICENSE.
