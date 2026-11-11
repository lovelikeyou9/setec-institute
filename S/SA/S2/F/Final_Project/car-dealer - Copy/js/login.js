function login(){

    fetch("http://localhost:3000/login", {

        method:"POST",

        headers:{
            "Content-Type":"application/json"
        },

        body:JSON.stringify({

            email:
            document.getElementById("email").value,

            password:
            document.getElementById("password").value

        })

    })

    .then(res => res.json())

    .then(data => {

        if(data.user){

            localStorage.setItem(
                "user",
                JSON.stringify(data.user)
            );

            alert(data.message);

            window.location =
            "main.html";

        }else{

            alert(data.message);

        }

    });

}