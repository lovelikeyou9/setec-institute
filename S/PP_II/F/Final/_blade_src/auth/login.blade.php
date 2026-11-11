@extends('layouts.store')
@section('title', 'Login — MMT')
@section('content')
<div class="auth-page">
  <div class="auth-card">
    <div class="auth-brand">
      <img src="{{ asset('images/mme-icon.png') }}" alt="" width="44" height="44"/>
      <div>
        <strong>MMT</strong>
        <span>Modern Multiple Technology</span>
      </div>
    </div>
    <h1>Welcome back</h1>
    <p class="subtitle">Sign in to your account</p>
    <p class="subtitle" style="margin-top:-.35rem;font-size:.9rem;color:var(--text-muted)">
      New seller? <a href="{{ url('/register') }}">Register as a customer first</a>, then apply to sell.
    </p>

    @if(session('message'))
      <div class="alert alert-success" data-keep-alert>{{ session('message') }}</div>
    @endif
    @if(session('error'))
      <div class="alert alert-error" data-keep-alert>{{ session('error') }}</div>
    @endif

    <form action="{{ url('/login') }}" method="post" class="auth-form">
      @csrf
      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required autocomplete="username"
               placeholder="you@email.com" value="{{ old('email') }}"/>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required autocomplete="current-password"
               placeholder="Your password"/>
      </div>
      <label style="display:flex;align-items:center;gap:.4rem;font-size:.9rem;margin-bottom:1rem">
        <input type="checkbox" name="remember" value="1" style="width:auto;margin:0"/> Remember me
      </label>
      <button type="submit" class="btn btn-primary btn-block auth-submit">Login</button>
    </form>

    <p class="auth-switch">
      New to MMT?
      <a href="{{ url('/register') }}">Create an account</a>
    </p>
  </div>
</div>
@endsection
