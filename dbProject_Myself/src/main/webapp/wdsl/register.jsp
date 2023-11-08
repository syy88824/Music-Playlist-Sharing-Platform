<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="BIG5">
<title>Register page</title>
<style>
body{
    width: 100%;
    height: 100%
    margin: 0;
    padding: 0;
    background-color: #ccc6c5;
    margin: auto;
    position:absolutes;
}
.solidWord{
    color:#5c1700;
    -webkit-text-stroke:transparent;
}
.titleWord{
    font-weight:bolder;
    font-size: 7ch;
    font-family:'Poppins','Trebuchet MS', fantasy, 'Times New Roman';
    font-variation-settings: 'wght' 1000, 'slnt' 0;
    -webkit-text-stroke: 2px #5c1700; /* Chrome, Safari, Opera */
    color: transparent;
}
.rectangle{
    width: 800px;
    height: 670px;
    background-color: rgba(255, 255, 255, 0.5);
    margin-top: 6%;
    margin-left: 24%;
    text-align: center;
    border-radius: 20px;
}
.word{
    opacity: 1;
    color: #5c1700;
    font-weight:900;
    font-size:xx-large;  
    font-family:'Poppins','Trebuchet MS', fantasy, 'Times New Roman';
    font-variation-settings: 'wght' 700, 'slnt' 0;
}
#smallWord{
    font-size: x-large;
}
input{
    border-color: #5c1700;
    border-width: 3px;
    height: 35px;
    width: 300px;
    border-radius: 20px;
    color: rgb(77, 66, 66);
    font-family: 'Poppins', 'Trebuchet MS', fantasy, 'Times New Roman';
    font-size: x-large;
    padding-left: 15px;
}
.btnSubmit{
    border-radius: 20px;
    border-color: #5c1700;
    border-width: 3px;
    width: 150px;
    height: 50px;
}
button:hover{
   background-color:#67412e;
    color: rgb(219, 204, 204); 
}
</style>
</head>
<body>
	    <div class = 'rectangle'>
        <br></br>
        <h1 class="titleWord">Re<span class="solidWord">g</span>is<span class="solidWord">t</span>er</h1>
        <br></br>
        <br></br>
        <p>
            <span class="word">Account：</span>           
            <input type="text" name="txtAccount">
        </p>
        <br></br>
        <p>
            <span class="word">Password：</span> 
            <input type="password" name="pwAccount">
        </p>
        <br></br>
        <p>
            <span class="word" id="smallWord">Reenter your Password：</span> 
            <input type="password" name="pwReenter">
        </p>
        <br>
        <button type="button" class="word btnSubmit">Sign up</button>
        <br></br>
    </div>
</body>
</html>