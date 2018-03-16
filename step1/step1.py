import lxml.html
import requests

def main(args):
    compare = args.get("compare")
    pusheremail = args.get("pusher").get("email")
    pushername = args.get("pusher").get("name")
    print(compare)
    print("Pusher "+pushername+" "+pusheremail)
    r = requests.post("https://us-central1-hello-cloud-196820.cloudfunctions.net/helloCloud", 
                      data={'compare': compare, 'pushername': pushername, 'pusheremail': pusheremail})
    print(r.status_code, r.reason)
    return {"compare": compare}
