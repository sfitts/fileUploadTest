package main

import (
	"io"
	"net/http"
    "encoding/json"
	"os"
)

type Result struct {
	Success bool
	Message string
}

//This is where the action happens.
func uploadHandler(w http.ResponseWriter, r *http.Request) {
	success := Result{true, "All good"}

	switch r.Method {

	//POST takes the uploaded file(s) and saves it to disk.
	case "POST":
		//get the multipart reader for the request.
		reader, err := r.MultipartReader()

		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		//copy each part to destination.
		for {
			part, err := reader.NextPart()
			if err == io.EOF {
				break
			}

			//if part.FileName() is empty, skip this iteration.
			if part.FileName() == "" {
				continue
			}
			dst, err := os.Create("/tmp/" + part.FormName())
			defer dst.Close()

			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			if _, err := io.Copy(dst, part); err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		}

		//display success message.
		js, err := json.Marshal(success)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		w.Write(js)

	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
}

func main() {
	http.HandleFunc("/funf_connector/set_funf_data", uploadHandler)

	//Listen on port 8002
	http.ListenAndServe(":8002", nil)
}
