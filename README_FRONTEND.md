# This is about setting up and current state of the frontend app. The proper README is the other one.

## What needs to be done before deployment?

Go to `api.yml` and set the namespace that the frontend app will use for backend requests. Currently, it is set
to `localhost:8080`.
Also, make sure that `api.yml` is accessible with all the schemas (which you can find
in `src/main/resources/schemas/openapi-schemas`) and update the API generation script in `package.json`. To only
generate
the API without deploying the app, you may then run `npm run prestart`. Eventually edit `api.yml` to point the schema
references to the right path.

## Is there something that the frontend app does in the background?

Yes. The frontend app pings the endpoint `info-finmath` to see if the valuation service is available or not. If the ping
request is not responded with code `200`, the valuation service is supposed to be unavailable.

## What resources are part of the frontend app and are not served by the backend?

- selectable parties info
- a catalogue of global currencies
- selectable day count fractions
- selectable fixing day offsets
- a catalogue of RIC for calibration items recognized by the valuation service
- a catalogue of payment frequencies correctly handled by the valuation service

## Is the frontend app production ready?

Far from it. There are many issues that need attention:

- at some DPI settings the application looks weird. With very aggressive rescaling, the app crashes altogether.
- the user credentials are embedded in the code. This was done because the valuation service is the component that
  handles authentication and session information, but the frontend app is served on a different port than the valuation
  service. Maybe the frontend app should refuse to load until authentication is completed and then gather the session
  info from the server? Anyway, this is not a long term solution.
- the validation of the user forms uses some custom logic that is a bit clunky. A refactoring in order to better use
  Angular validators is advised.
- the UI used a fixed number of decimal places for currencies, ignoring the currency standard even though the
  information is available.
- date arithmetic sometimes does not work properly (in particular when the difference between two dates is less than 12
  hours). This does not affect valuation, only some UI elements might give misleading information.
- there is a complex system of page routes to load the main part of the app. This seemed a good idea in the beginning,
  but maybe needs changing?

All of these issues have a TODO associated with them in the code. Do a project wide search (actually, just
the `frontend/src/**`
part) to see where they are.
