from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import pandas as pd

app = FastAPI()

model = joblib.load("traffic_isolation_forest.pkl")

class TrafficPredictRequest(BaseModel):
    hour: int
    inVolume: int
    outVolume: int
    totalVolume: int
    dayOfWeek: int

@app.post("/predict")
def predict(request: TrafficPredictRequest):
    data = pd.DataFrame([{
        "hour": request.hour,
        "inVolume": request.inVolume,
        "outVolume": request.outVolume,
        "totalVolume": request.totalVolume,
        "dayOfWeek": request.dayOfWeek
    }])

    prediction = int(model.predict(data)[0])
    score = float(model.decision_function(data)[0])

    anomaly = bool(prediction == -1)

    return {
        "anomaly": anomaly,
        "aiScore": score,
        "status": "ANOMALY" if anomaly else "NORMAL"
    }