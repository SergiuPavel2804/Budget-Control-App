import numpy as np
from sklearn.linear_model import LinearRegression

def main(reportValuesString):

    reportValues = reportValuesString[1:-1].split(", ")

    X = np.arange(len(reportValues)).reshape(-1, 1)
    y = np.array(reportValues)

    model = LinearRegression()
    model.fit(X, y)

    future_time_steps = 1
    future_X = np.arange(len(reportValues), len(reportValues) + future_time_steps).reshape(-1, 1)
    predictions = model.predict(future_X)

    return round(float(predictions[0]), 2)


