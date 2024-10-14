const express = require('express');
const app = express();

const port = 3000

//设置跨域访问
app.all("*", function(req, res, next) {
	//设置允许跨域的域名，*代表允许任意域名跨域
	res.header("Access-Control-Allow-Origin", req.headers.origin || '*');
	 // //只允许http://xxx.xx.xx/可跨
    //res.header('Access-Control-Allow-Origin', 'http://xxx.xx.xx/');
	//允许的header类型
	res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
	//跨域允许的请求方式 
	res.header("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
	// 可以带cookies
	res.header("Access-Control-Allow-Credentials", true);
	if (req.method == 'OPTIONS') {
		res.sendStatus(200);
	} else {
		next();
	}
})

app.use(express.static("public"));
app.get('/events', function(req, res) {
  res.writeHead(200, {
    'Content-Type': 'text/event-stream',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive'
  });
  // 每隔1秒钟，写出1条数据
  setInterval(() => {
    res.write(`data: ${new Date().toLocaleTimeString()}\n\n`);
  }, 1000);
});
app.listen(port, () => {
  console.log(`SSE server running on port :${port}`);
});