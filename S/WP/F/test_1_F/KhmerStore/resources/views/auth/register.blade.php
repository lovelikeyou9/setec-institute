<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - KhmerStore</title>
    <link rel="stylesheet" href="{{ asset('css/template.css') }}">
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
    <link rel="stylesheet" href="{{ asset('css/auth.css') }}">
</head>
<body class="auth-body">
    <div class="auth-card">
        <div class="auth-brand">
            <h1>KhmerStore</h1>
            <p>Create Admin Account</p>
        </div>

        @if($errors->any())
            @foreach ($errors->all() as $error)
                <div class="error">{{ $error }}</div>
            @endforeach
        @endif

        <form class="product-form" action="{{ route('register') }}" method="POST">
            @csrf
            <div class="form-group full-width">
                <label for="name">Name</label>
                <input type="text" id="name" name="name" value="{{ old('name') }}"
                       placeholder="Your full name" required autofocus>
            </div>

            <div class="form-group full-width">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="{{ old('email') }}"
                       placeholder="you@example.com" required>
            </div>

            <div class="form-group full-width">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="At least 6 characters" required>
            </div>

            <div class="form-group full-width">
                <label for="password_confirmation">Confirm Password</label>
                <input type="password" id="password_confirmation" name="password_confirmation"
                       placeholder="Re-enter password" required>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-submit">Register</button>
            </div>
        </form>

        <p class="auth-switch">
            Already have an account?
            <a href="{{ route('login') }}">Login</a>
        </p>
    </div>
</body>
</html>
