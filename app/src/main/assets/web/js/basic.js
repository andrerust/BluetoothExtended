function showInfo()
{
    var str1= document.getElementById('fname').value;
    var str2= document.getElementById('lname').value;
    var res = str1.concat(' '+str2);
    alert('Hi,' +res);
}