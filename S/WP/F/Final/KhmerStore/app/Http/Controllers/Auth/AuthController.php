<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class AuthController extends Controller
{
    public function showLogin()
    {
        return view('auth.login');
    }

    public function login(Request $request)
    {
        $credentials = $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $user = User::where('email', $credentials['email'])->first();
        if ($user && ! $user->enabled) {
            return back()->withInput()->with('error', 'Your account is disabled.');
        }

        if (Auth::attempt($credentials, $request->boolean('remember'))) {
            $request->session()->regenerate();
            $user = Auth::user()->loadMissing('seller');
            if ($user->isAdmin()) {
                return redirect()->intended('/admin');
            }

            // Approved sellers → Seller Center. Suspended stores keep login as customers.
            if ($user->isApprovedSeller()) {
                return redirect()->intended('/seller-dashboard');
            }

            $seller = $user->seller;
            if ($seller && $seller->status === 'SUSPENDED') {
                return redirect('/account')->with(
                    'message',
                    'Welcome back. Your store is suspended — you can shop, but selling is disabled until an admin restores it.'
                );
            }

            return redirect()->intended('/account');
        }

        return back()->withInput()->with('error', 'Invalid email or password.');
    }

    public function showRegister()
    {
        return view('auth.register');
    }

    public function register(Request $request)
    {
        $data = $request->validate([
            'full_name' => 'required|string|max:120',
            'email' => 'required|email|max:100|unique:users,email',
            'phone' => 'nullable|string|max:30',
            'password' => 'required|string|min:6|confirmed',
        ]);

        $user = User::create([
            'full_name' => $data['full_name'],
            'email' => $data['email'],
            'phone' => $data['phone'] ?? null,
            'password' => $data['password'],
            'role' => 'CUSTOMER',
            'enabled' => 1,
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        Auth::login($user);
        $request->session()->regenerate();

        return redirect('/account')->with('message', 'Welcome to MMT!');
    }

    public function logout(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect('/')->with('message', 'Logged out.');
    }
}
