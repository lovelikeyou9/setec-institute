import requests

TELEGRAM_BOT_TOKEN = "8534615271:AAF5OUxrVNusHF8x166X41uP0ZoNJZzsibg"
TELEGRAM_CHAT_ID = "@sv2_6_pp_ii_test_3_f"


def send_telegram(message):
    url = f"https://api.telegram.org/bot{TELEGRAM_BOT_TOKEN}/sendMessage"
    payload = {
        "text": message,
        "parse_mode": "HTML",
        "chat_id": TELEGRAM_CHAT_ID,
        "disable_web_page_preview": True,
        "disable_notification": False,
        "reply_to_message_id": None,
    }
    headers = {
        "accept": "application/json",
        "User-Agent": "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)",
        "content-type": "application/json",
    }
    try:
        requests.post(url, json=payload, headers=headers, timeout=5)
    except Exception:
        # Do not break the page if Telegram is down
        pass
