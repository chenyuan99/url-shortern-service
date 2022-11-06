### Usage

Import TokenRepo, ShortUrlRepo from 'database' module 

for instance

```python
from database import ShortUrlRepo, TokenRepo
```

most of the case, we do NOT need to import TokenRepo. The main two functions are included in ShortUrlRepo:

- createShortUrl (originalUrl: str, domain: str, duration: int) -> object
- getOriginalUrl (shortUrl: str) -> str

### Eaxmple

example.py

```python
from database import ShortUrlRepo

ShortUrlRepo.createShortUrl("www.baidu.com", "urlService", 2)

print(ShortUrlRepo.getOriginalUrl("http://urlService.com/s9n6Qu1z"))
```

