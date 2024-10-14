import axios from 'axios';

export default async function initial(){
  console.log('initial start!')
  const url = 'http://123.60.77.90:9010/';
  console.log(url)
  let res;
    if(!sessionStorage.getItem('worddata')){
        res = await axios.get(url+'spy/news/keyword',{
          params:{
              // date:new Date().toISOString().slice(0, 10)
              date:'2024-06-15'
          }
        })
        console.log(res)
        sessionStorage.setItem('worddata',JSON.stringify(res))
    }
    if(!sessionStorage.getItem('timeline')){
        res = await axios.get(url+'spy/news/14num',{
          params:{
              // date:new Date().toISOString().slice(0, 10)
              date:'2024-06-15'
          }
        })
        sessionStorage.setItem('timeline',JSON.stringify(res))
    }
    if(!sessionStorage.getItem('guage')){
        res = await axios.get(url+'spy/news/standpoint',{
          params:{
              date:'2024-06-15'
          }
        })
        sessionStorage.setItem('guage',JSON.stringify(res))
    }
    if(!sessionStorage.getItem('bar')){
        res = await axios.get(url+'spy/news/standpoint',{
          params:{
              date:'2024-06-15'
          }
        })
        sessionStorage.setItem('bar',JSON.stringify(res))
      }
      if(!sessionStorage.getItem('category')){
        res = await axios.get(url+'spy/news/2days/category',{
          params:{
              // date:new Date().toISOString().slice(0, 10)
              date:'2024-06-15'
          }
          })
        sessionStorage.setItem('category',JSON.stringify(res))
      }
      if(!sessionStorage.getItem('map')){
        res = await axios.get(url+'spy/news/provinceCount',{
          params:{
              // date:new Date().toISOString().slice(0, 10)
              date:'2024-06-15'
          }
          })
        sessionStorage.setItem('map',JSON.stringify(res))
      }  
      if(!sessionStorage.getItem('14num')){
        res =await axios.get(url+'spy/news/14num', {
          params: {
             date:'2024-06-15'
          }
        });
        sessionStorage.setItem('14num',JSON.stringify(res))
      }
      if(!sessionStorage.getItem('totalnum')){
        res =await axios.get(url+'spy/news/count');
        sessionStorage.setItem('totalnum',JSON.stringify(res))
      }
      console.log('initial end!')
}