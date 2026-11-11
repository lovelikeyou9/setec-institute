


from pathlib import Path
import os
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

folder = Path(__file__).parent
source = folder / "Final_CND_II.zip"
encrypted = folder / "Final_CND_II.encrypted"
key_file = folder / "secret.key"

if not source.exists():
    raise FileNotFoundError("Final_CND_II.zip was not found.")

if encrypted.exists() or key_file.exists():
    raise FileExistsError(
        "Encrypted output or key already exists. "
        "Do not overwrite it; back up the existing key first."
    )

# Generate a random 256-bit key
key = AESGCM.generate_key(bit_length=256)

# Generate a fresh 12-byte nonce
nonce = os.urandom(12)

# Encrypt the ZIP
cipher = AESGCM(key)
plaintext = source.read_bytes()
ciphertext = cipher.encrypt(nonce, plaintext, None)

# Save the key separately
key_file.write_bytes(key)

# Save nonce + ciphertext in one file
encrypted.write_bytes(nonce + ciphertext)

print("Encryption successful.")
print("Encrypted file:", encrypted.name)
print("Secret key:", key_file.name)
print("Keep secret.key safe and private!")


