<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - KhmerStore</title>
    <link rel="stylesheet" href="{{ asset('css/template.css') }}">
    <link rel="stylesheet" href="{{ asset('css/form.css') }}">
    <link rel="stylesheet" href="{{ asset('css/auth.css') }}">
</head>
<body class="auth-body">
    <div class="auth-card">
        <div class="auth-brand">
            <h1>KhmerStore</h1>
            <p>Admin Login</p>
        </div>

        @if(session('success'))
            <div class="auth-alert success">{{ session('success') }}</div>
        @endif

        @if($errors->any())
            @foreach ($errors->all() as $error)
                <div class="error">{{ $error }}</div>
            @endforeach
        @endif

        <form class="product-form" action="{{ route('login') }}" method="POST">
            @csrf
            <div class="form-group full-width">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="{{ old('email') }}"
                       placeholder="you@example.com" required autofocus>
            </div>

            <div class="form-group full-width">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Enter your password" required>
            </div>

            <div class="checkbox-group auth-remember">
                <input type="checkbox" id="remember" name="remember" value="1"
                       {{ old('remember') ? 'checked' : '' }}>
                <label for="remember">Remember me</label>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-submit">Login</button>
            </div>
        </form>

        <p class="auth-switch">
            No account yet?
            <a href="{{ route('register') }}">Register</a>
        </p>
    </div>
</body>
</html>
