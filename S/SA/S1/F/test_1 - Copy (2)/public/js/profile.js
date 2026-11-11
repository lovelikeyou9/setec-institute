document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token");

  if (!token) {
    window.location.href = "/login.html";
    return;
  }

  const res = await fetch("http://localhost:3000/api/users/profile", {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  const user = await res.json();

  document.getElementById("profileName").innerText = user.name;
  document.getElementById("profileEmail").innerText = user.email;
  document.getElementById("profilePhone").innerText = user.phone;
});
