# Effect Network LabelBox Scripts

This script can also download images from the LabelBox instance and
upload them to a private IPFS instance, to ensure the images stay
accessible and behind HTTPS. For this to work, you must set your
LabelBox session ID which can be fetched from the HTTP requests in an
authenticated LabelBox instance:

```
export LB_SESSION_ID=".eJ....."
```

### Examples

```
bb convert project-5-at-2023-06-01-15-26-9429f31c.json
```
