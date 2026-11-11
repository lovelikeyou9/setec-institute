


from pathlib import Path
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

folder = Path(__file__).parent

encrypted_file = folder / "Final_CA_II.encrypted"
key_file = folder / "secret.key"
output_file = folder / "Final_CA_II-decrypted.zip"

key = key_file.read_bytes()
data = encrypted_file.read_bytes()

# First 12 bytes are the nonce
nonce = data[:12]
ciphertext = data[12:]

# Decrypt using AES-256-GCM
cipher = AESGCM(key)
plaintext = cipher.decrypt(nonce, ciphertext, None)

output_file.write_bytes(plaintext)

print("Decryption successful!")
print("Restored:", output_file.name)
