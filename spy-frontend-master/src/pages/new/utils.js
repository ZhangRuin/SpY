export function generateRandomGradient() {
    // 生成随机的RGB颜色值
    function getRandomColor() {
      return 128+Math.floor(Math.random() * 128);
    }
  
    // 生成CSS渐变背景色字符串
    function generateGradientString() {
      var color1 = `rgb(${getRandomColor()}, ${getRandomColor()}, ${getRandomColor()})`;
      return `${color1}`;
    }
    return generateGradientString();
}