import re

# Leer la primera línea del archivo
with open("transiciones.txt", "r") as f:
    primera = f.readline()

# Eliminar posibles espacios o salto de línea al inicio/final
linea = primera.strip()

regex = '(T0)(.*?)(T1)(?![01])(.*?)((T2)(.*?)(T3)(.*?)(T4)(.*?)|(T5)(.*?)(T6)(.*?)|(T7)(.*?)(T8)(.*?)(T9)(.*?)(T10)(.*?))(T11)(.*?)'
sub = '\g<2>\g<4>\g<7>\g<9>\g<11>\g<13>\g<15>\g<17>\g<19>\g<21>\g<23>\g<25>'

while True:
    try:
        nueva_linea, count = re.subn(regex, sub, linea)
    except re.error as e:
        print(f"Error en la regex: {e}")
        break
    print("Iteración reemplazos:", count, "->", nueva_linea)
    if count == 0:
        break
    linea = nueva_linea

print("Resultado final:", linea)
