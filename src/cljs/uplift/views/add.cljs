(ns uplift.views.add
  (:require-macros [dommy.macros :refer [deftemplate]]))

(deftemplate lift-body [type]
  [:div.lift.row
   [:div.small-12.columns
    [:label "Weight" [:input.weight]]
    [:label "Sets" [:input.sets]]
    [:label "Reps" [:input.reps]]]])

(deftemplate activity-template [activity-name]
  [:div.lift.row
   [:div.small-6.small-offset-3.columns
    [:h3 activity-name]
    [:a.button.new-lift-body]]])

(deftemplate day-template [date]
  [:div.day.row {:class (str "date-" date)}
   [:div.small-8.small-offset-2.columns
    [:div.row.collapse
     [:h1 date]]
    [:div.row.collapse
     [:div.small-10.columns
      [:input.lift-type {:type "text" :placeholder "Lift Name"}]]
     [:div.small-2.columns
      [:a.button.postfix.new-lift "New Lift"]]]]])
