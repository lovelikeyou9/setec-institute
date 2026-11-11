function register(){

    fetch("http://localhost:3000/register", {

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            username:
            document.getElementById("username").value,

            email:
            document.getElementById("email").value,

            password:
            document.getElementById("password").value

        })

    })

    .then(res => res.json())

    .then(data => {

        alert(data.message);

        window.location =
        "login.html";

    });

}