# запуск: uvicorn main:app --reload
# страница: http://127.0.0.1:8000/registration
from fastapi import FastAPI, UploadFile, File
import random as rd
from fastapi.responses import FileResponse
from PIL import Image
import numpy as np
import shutil
from tensorflow.keras.preprocessing import image
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D
from tensorflow.keras.layers import Activation, Dropout, Flatten, Dense
from tensorflow.keras.models import load_model

app = FastAPI()


@app.get("/")
def root():
    return FileResponse("pages/index.html")

@app.get("/registration")
def root():
    return FileResponse("pages/registration.html")

@app.post("/afterregistr")
def root():
    return FileResponse("pages/afterregistr.html")

@app.get("/here")
def root():
    return FileResponse("pages/here.html")

@app.post("/uploadimg/")
def upload_usr_img(file_upload: UploadFile):
    file_path = f"C:/Users/user5/Documents/savedfiles/{file_upload.filename}"
    with open(file_path, "wb") as file_object:
        shutil.copyfileobj(file_upload.file, file_object)
    checker, value, defect, helpa = check_img(file_path)
    return {"filename": file_upload.filename, "quality": checker, "brightness": value, "defect": defect, "recomend": helpa}

@app.post("/uploadsound/")
def upload_usr_sound(file_upload: UploadFile):
    defec, info = def_sound()
    return {"Название": file_upload.filename, "Дефект": defec, "Доп информация": info}

@app.get("/testconnect")
def gimmeunswer():
    return {"Тест1": 1, "Тест2": "2", "Тест3": [3,4]}

def def_sound():
    mainrand = rd.randint(0,100)
    if mainrand < 16:
        return "-", "Проверьте данные, наблюдается шум"
    if mainrand < 31:
        return "-", "Проверьте данные, стучат не по металлу"
    defec = ["Есть", "Нет"]
    ver = rd.randint(0,1)
    if defec[ver] == "Есть":
        return (f"{defec[ver]}", f"Вероятность {rd.randint(1, 100)}%")
    return (f"{defec[ver]}", f"-")

def check_img(file_path):
    with Image.open(file_path) as immg:
        immg.load()
        mean_intensity = np.mean(immg)
        brightness = mean_intensity
        if brightness > 150:
            return "Слишком ярко, данное изображение заблокировано для оценки", brightness, "-", "-"
        if brightness < 50:
            return "Слишком темно, данное изображение заблокировано для оценки", brightness, "-", "-"
        defect, helpa = check_defect(file_path)
        return "Хорошее", brightness, defect, helpa

def check_defect(file_path):


    final = {}
    final_helpa = []
    '''
    # Что было раньше
    defects = ["Ассиметрия углового шва", "Дефект отсутствует", "Брызги",
                "Кратер", "Наплыв", "Непровар",
                "Подрез", "Поры", "Прожог",
                "Скопление включений", "Трещина", "Трещина поперечная",
                "Трещина сварного соединения", "Шлаковые включения"]
    '''
    defects = [ "Шлаковые включения", "Дефект отсутствует", "Брызги",
                "Кратер", "Наплыв","Непровар",
                "Подрез", "Поры", "Прожог",
                "Поры", "Трещина", "Трещина",
                "Трещина", "Шлаковые включения"]
    helpa = ["Улучшите защиту шва при сварке в среде защитных газов", "Вы молодец!", "Варите короткой дугой / снизьте напряжение",
             "Установите плавное гашение дуги", "Измените наклон плоскости наложения шва", "Проверьте сборку детали или конструктив шва",
             "Измените угол электрода", "Не используйте влажные флюсы или электроды", "Проверьте режим сварки, возможно недостаточное тепловложение",
             "Не используйте влажные флюсы или электроды", "Измените скорость охлаждения детали", "Измените скорость охлаждения детали",
             "Измените скорость охлаждения детали", "Улучшите защиту шва при сварке в среде защитных газов"]
    for i in range(9):
        winner = load_model("winner.h5")
        img = image.load_img(file_path, target_size=(150, 250))
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0)
        preds = winner.predict(x)
        defec = defects[preds[0].argmax()]
        print(defec, preds[0].argmax())
        if i == 0 and defec == "Дефект отсутствует":
            final[defec] = "-"
            final_helpa.append(helpa[preds[0].argmax()])
            break
        if defec in final:
            num = final[defec]
            num += 11
            final[defec] = num
        else:
            final[defec] = 11
            final_helpa.append(helpa[preds[0].argmax()])



    return final, final_helpa


