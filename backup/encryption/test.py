import rncryptor
import base64
data = 'Peyote is the champion!!!'
password = 'None'
cryptor = rncryptor.RNCryptor()
encrypted_data = cryptor.encrypt(data, password)
print base64.b64encode(encrypted_data)
encoded = base64.b64decode("AwERLgVAgeh2r/eYNupP6h/bdt+/04vl2anmbzrfrwIm8Fu4AmGBNTEZ1X+gwbOoJxbg+we/jT/Cby4BWe202+Tkhzn10yfBMhWD3+7s722FG2gUUQKlErNzSAQAjBnY/uE=")
decrypted_data = cryptor.decrypt(encoded, password)
print decrypted_data
