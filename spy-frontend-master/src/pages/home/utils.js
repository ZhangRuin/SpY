export function generateRandomGradient() {
    // 生成随机的RGB颜色值
    function getRandomColor() {
      return Math.floor(Math.random() * 256);
    }
  
    // 生成CSS渐变背景色字符串
    function generateGradientString() {
      var color1 = `rgb(${getRandomColor()}, ${getRandomColor()}, ${getRandomColor()})`;
      var color2 = `rgb(${getRandomColor()}, ${getRandomColor()}, ${getRandomColor()})`;
      return `linear-gradient(to bottom right, ${color1}, ${color2})`;
    }
    return generateGradientString();
}