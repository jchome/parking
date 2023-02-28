from flask import Flask, request
import urllib.request
import json
import geopy.distance
from glom import glom, Coalesce, Iter

app = Flask(__name__)


def parkings_Poitiers():
    """
  Get all parkings of Poiters, as list of dict objects

  Usage:
  curl 'http://127.0.0.1:5000/nearOfPoint?lat=46.58570214260076&lon=0.35044408926683884&limit=1'

  curl 'http://127.0.0.1:5000/all'
  
  """
    webURL = urllib.request.urlopen(
        'https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilites-stationnement-des-parkings-en-temps-reel&facet=nom'
    )
    data = webURL.read()
    encoding = webURL.info().get_content_charset('utf-8')
    JSON_object = json.loads(data.decode(encoding))
    ## Solution 1: extract data manually
    # records = JSON_object['records']
    # parkings = []
    # for d in records:
    #     parking = {}
    #     fields = d["fields"]
    #     parking["name"] = fields["nom"]
    #     if "geo_point_2d" in fields:
    #         parking["lat"] = fields["geo_point_2d"][0]
    #         parking["lon"] = fields["geo_point_2d"][1]
    #     else:
    #         continue
    #     parking["capacity"] = fields["capacite"]
    #     parking["vacancy"] = fields["places"]
    #     parkings.append(parking)
    # return parkings

    ## Solution 2: use glom
    spec = {
        'results': ('records', [{
            'name': 'fields.nom',
            'capacity': 'fields.capacite',
            'lat': (Coalesce('fields.geo_point_2d',
                             default=(0, 0)), Iter().first()),
            'lon': (Coalesce('fields.geo_point_2d',
                             default=(0, 0)), Iter().slice(1,2).first()),
            'vacancy': 'fields.places'
        }])
    }
    return glom(JSON_object, spec)['results']


@app.route('/all')
def index():
    """
  Return all parkings of the URL request
  """
    return parkings_Poitiers()


@app.route('/nearOfPoint')
def parkings_near_of():
    """
  Return sorted parkings of the URL request, limited to a number of results
  """
    lat = float(request.args.get("lat"))
    lon = float(request.args.get("lon"))
    limit = int(request.args.get("limit")) if request.args.get("limit") is not None else None
    ## Filter parkings to have a "lat" and "lon" data
    all_parkings = [ p for p in parkings_Poitiers() if p["lat"] is not None and p["lon"] is not None]
    
    ## Sort by distance from the point
    data = sorted(all_parkings,
                  key=lambda parking: geopy.distance.geodesic(
                      (float(parking["lat"]), float(parking["lon"])),
                      (lat, lon)).km * 1000)
    
    ## Cut the result list
    if limit:
        return data[0:limit]
    else:
        return data


if __name__ == "__main__":
    app.run()
    
