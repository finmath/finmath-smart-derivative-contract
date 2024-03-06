import { Component, OnInit } from "@angular/core";
import { interval } from "rxjs";
import { singleSpaPropsSubject } from "src/single-spa/single-spa-props";

/**
 * Application root component
 */
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
})
export class AppComponent implements OnInit {
  public title = "sdc-frontend";
  public isAuthed = false;

  public flipAuth(status: boolean){
    this.isAuthed = status;
  }

  private authTest(){
    var timer = interval(5000);
    var credentials = prompt("Please enter your credentials: ","user:password");
    var splitCredentials = credentials?.split(":");
    var username=splitCredentials![0];
    var password=splitCredentials![1];
    var xhttp= new XMLHttpRequest();
    var fn = this.flipAuth;
    var ref = this;
    xhttp.withCredentials = true;
    xhttp.onreadystatechange = function () {
      console.log(this.readyState);
      var sub = timer.subscribe(()=>{if (this.readyState === 4) {
        if (this.status === 200) {
          sessionStorage.setItem("username",username);
          sessionStorage.setItem("password",password);
          fn.call(ref,true);
          sub.unsubscribe();
        } else {
          console.log("Error", this.statusText);
          sub.unsubscribe();
          window.location.reload();
        } 
    }else {
      console.log("Fail"); 
      sub.unsubscribe();
      window.location.reload();
    }});
      
  };

  xhttp.open("GET","http://localhost:8080/info/git",true,username,password);
  xhttp.send();

  }

  public ngOnInit(): void {
    singleSpaPropsSubject.subscribe((props: any) => {
      if(props.standalone) {
        this.authTest();
      } else {
        //this auth comes from sdc-service
        const basicAuthData = window.localStorage.getItem('basic-auth-data');
        if(basicAuthData) {
          this._setValuationServiceAuth();
        } else {
          this._unsetValuationServiceAuth();
        }
      }
      window.addEventListener('isSignedIn',(event: any) => {
        this.isAuthed = event.detail;
        if(event.detail) {
          this._setValuationServiceAuth();
        } else {
          this._unsetValuationServiceAuth();
        }
      });
    }); 
  }

  //@todo: rewrite it after getting AccessToken
  private _setValuationServiceAuth() {
    this.isAuthed = true;
    sessionStorage.setItem("username",'user1');
    sessionStorage.setItem("password",'password1');
  }

  //@todo: rewrite it after getting AccessToken
  private _unsetValuationServiceAuth() {
    this.isAuthed = false;
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("password");
  }
}

