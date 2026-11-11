<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleMiddleware
{
    /**
     * @param  string  ...$roles  ADMIN|SELLER|CUSTOMER
     */
    public function handle(Request $request, Closure $next, string ...$roles): Response
    {
        $user = $request->user();
        if (! $user) {
            return redirect()->route('login')->with('error', 'Please log in.');
        }
        if (! $user->enabled) {
            auth()->logout();

            return redirect()->route('login')->with('error', 'Your account is disabled.');
        }
        $allowed = array_map('strtoupper', $roles);
        $role = strtoupper((string) $user->role);
        if (! in_array($role, $allowed, true)) {
            abort(403, 'Unauthorized for this area.');
        }

        // Banned / pending sellers must not use Seller Center (admins still can).
        if ($role === 'SELLER' && in_array('SELLER', $allowed, true)) {
            $seller = $user->seller;
            if (! $seller || $seller->status !== 'APPROVED') {
                $msg = match ($seller->status ?? '') {
                    'SUSPENDED' => 'Your seller account has been suspended by an administrator.',
                    'PENDING' => 'Your seller application is still pending approval.',
                    'REJECTED' => 'Your seller application was rejected.',
                    default => 'Seller account required.',
                };

                return redirect('/account')->with('error', $msg);
            }
        }

        return $next($request);
    }
}
