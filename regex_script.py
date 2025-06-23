import re

linea = "T0T1T2T0T1T0T1T3T4T5T11T0T6T7T11T1T0T8T1T9T10T11T5T0T6T5T11T1T0T6T7T11T8T9T10T11T1T2T0T1T0T1T3T4T2T11T0T1T3T4T5T11T0T6T11T5T6T11T1T5T0T1T0T1T6T7T11T0T8T1T9T10T5T11T0T6T11T5T6T11"
# Patrón para capturar T0→T1→T2→T3→T4→T11, con texto variable entre ellos:
patron = re.compile(r'(T0)(.*?)(T1)(.*?)((T2)(.*?)(T3)(.*?)(T4)(.*?)|(T5)(.*?)(T6)(.*?)|(T7)(.*?)(T8)(.*?)(T9)(.*?)(T10)(.*?))(T11)(.*?)', re.DOTALL)

# Reemplazo: usa raw string y referencias válidas según tus grupos. 
# Aquí por ejemplo extraigo solo los textos intermedios: \2, \4, \6, \8, \10
reemplazo = r'\2\4\7\9\11\13\15\17\19\21\23\25'

while True:
    try:
        nueva_linea, count = patron.subn(reemplazo, linea)
    except re.error as e:
        print(f"Error en la regex: {e}")
        break
    print("Iteración reemplazos:", count, "->", nueva_linea)
    if count == 0:
        break
    linea = nueva_linea

print("Resultado final:", linea)
