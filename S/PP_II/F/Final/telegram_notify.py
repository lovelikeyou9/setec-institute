import logging
import threading

import requests
from flask import current_app


def _post(token, chat_id, message):
    url = f"https://api.telegram.org/bot{token}/sendMessage"
    payload = {
        "text": message,
        "parse_mode": "HTML",
        "chat_id": chat_id,
        "disable_web_page_preview": True,
        "disable_notification": False,
    }
    headers = {
        "accept": "application/json",
        "User-Agent": "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)",
        "content-type": "application/json",
    }
    try:
        resp = requests.post(url, json=payload, headers=headers, timeout=5)
        if not resp.ok:
            logging.getLogger("mmt.telegram").warning("Telegram HTTP %s: %s", resp.status_code, resp.text[:200])
        return resp.ok
    except Exception as exc:
        # Never break a page because Telegram is down
        logging.getLogger("mmt.telegram").warning("Telegram failed: %s", exc)
        return False


def send_telegram(message, wait=False):
    """Send an HTML message to the configured Telegram chat (non-blocking)."""
    try:
        cfg = current_app.config
        if not cfg.get("TELEGRAM_ENABLED"):
            return
        token = cfg.get("TELEGRAM_BOT_TOKEN")
        chat_id = cfg.get("TELEGRAM_CHAT_ID")
    except RuntimeError:
        return
    if not token or not chat_id:
        return
    if wait:
        return _post(token, chat_id, message)
    threading.Thread(target=_post, args=(token, chat_id, message), daemon=True).start()
