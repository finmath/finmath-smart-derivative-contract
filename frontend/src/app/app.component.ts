import { Component, OnInit } from "@angular/core";
import { interval } from "rxjs";

/**
 * Application root component
 */
@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
})
export class AppComponent implements OnInit {
  title = "sdc-frontend";
  isAuthed = false;

  flipAuth(status: boolean){
    this.isAuthed = status;
  }

  authTest(){
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

  ngOnInit(): void {
      this.authTest();
  }
}

