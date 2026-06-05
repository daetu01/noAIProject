import pandas as pd
from sklearn.ensemble import IsolationForest
import requests
import joblib

headers = {
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoiZGFpc3doYXRAZ21haWwuY29tIiwibmlja05hbWUiOiJ0b3RvZGxlIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzc5Nzc3NDg0LCJleHAiOjI2NDM3Nzc0ODR9.H_djEDqITS9Q1eKXdAnXN-XMoEhRzu5nklkradB5zso"
}

response = requests.get(
    "http://localhost:8080/api/traffic/dataset",
    headers=headers
)

response.raise_for_status()

df = pd.DataFrame(
    response.json()
)

features = [
    "hour",
    "inVolume",
    "outVolume",
    "totalVolume",
    "dayOfWeek"
]

X = df[features]

model = IsolationForest(
    n_estimators=100,
    contamination=0.05,
    random_state=42
)

model.fit(X)

df["anomaly"] = model.predict(X)
df["anomaly"] = df["anomaly"].map({
    1: False,
    -1: True
})

df["aiScore"] = model.decision_function(X)

print(df.head())
print(df["anomaly"].value_counts())

print(
    df[df["anomaly"] == True]
    [
        [
            "dayOfWeek",
            "hour",
            "inVolume",
            "outVolume",
            "totalVolume",
            "aiScore"
        ]
    ]
    .sort_values(
        "aiScore"
    )
    .head(10)
)

joblib.dump(model, "traffic_isolation_forest.pkl")