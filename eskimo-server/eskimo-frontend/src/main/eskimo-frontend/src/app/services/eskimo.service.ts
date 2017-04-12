import {Contest} from "../shared/contest";
import {Http} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";

@Injectable()
export class EskimoService {

    private urlHost = 'http://localhost:8080/api/';
    private urlContests = this.urlHost + 'contests';
    private urlContestCreate = this.urlHost + 'contest/create/from/zip';

    constructor(private http: Http) {
    }

    getContests(): Observable<Contest[]> {
        return this.http.get(this.urlContests)
            .map(res => res.json())
            .catch(this.handleError);
    }

    createContest(file: File): Observable<Contest> {
        let formData: FormData = new FormData();
        formData.append('file', file, file.name);
        return this.http.post(this.urlContestCreate, formData)
            .map(res => res.json())
            .catch(this.handleError);
    }

    private handleError(error: any) {
        console.error('errror', error);
        return Observable.throw(error.message || error);
    }

}