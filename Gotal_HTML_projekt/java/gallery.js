var prev = document.getElementById('previous');
var next = document.getElementById('next');

var arr = ["../images/slika1.jpg",
      "../images/slika2.jpg",
      "../images/slika3.jpg",
      "../images/slika4.jpg",
      "../images/slika5.jpg",
      "../images/slika6.jpg"
    ];

var i=0;

document.getElementById('frame').src=arr[0];
document.getElementById('body').style.backgroundImage = `url(${arr[0]})`;

next.addEventListener('click' , function(){
   i++;
  if(i > arr.length-1 ){
    i=0;
   }
document.getElementById('frame').src=arr[i];
document.getElementById('body').style.backgroundImage = `url(${arr[i]})`;
})

prev.addEventListener('click' , function(){
  i--;
  if(i<0){
    i= arr.length -1;
   }
document.getElementById('frame').src=arr[i];
document.getElementById('body').style.backgroundImage = `url(${arr[i]})`;
})