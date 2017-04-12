import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-contest',
  templateUrl: './new-contest.component.html',
  styleUrls: ['./new-contest.component.css']
})
export class NewContestComponent {

  constructor(private eskimoService: EskimoService, private router: Router) {
  }


  fileChange(event) {
    let fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.eskimoService.createContest(fileList[0]).subscribe(contest => this.router.navigateByUrl("/contests"));
    }
  }

}
