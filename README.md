# CMSSignedData Content Extractor

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
