import rncryptor
import base64
data = 'Peyote is the champion!!!'
password = '2work4fun!'
cryptor = rncryptor.RNCryptor()
encrypted_data = cryptor.encrypt(data, password)
print base64.b64encode(encrypted_data)
decrypted_data = cryptor.decrypt(encrypted_data, password)
print decrypted_data
